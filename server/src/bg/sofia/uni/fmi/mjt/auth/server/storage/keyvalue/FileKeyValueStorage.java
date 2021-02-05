package bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue;

import bg.sofia.uni.fmi.mjt.auth.server.storage.exception.StorageException;
import bg.sofia.uni.fmi.mjt.auth.server.storage.serializer.Serializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileKeyValueStorage<K, V> implements KeyValueDataStore<K, V> {

    private static final String TEMP_RECORDS_PREFIX = "temp-records";
    private static final String TEMP_RECORDS_SUFFIX = "";

    private static final String KV_DELIM = " ";
    private static final int RECORD_PARTS_COUNT = 2;
    private static final int RECORD_KEY_INDEX = 0;
    private static final int RECORD_VALUE_INDEX = 1;

    public static final String FAILED_TO_CREATE_TEMP_FILE = "Failed to create temp file.";
    public static final String FAILED_TO_OPEN_TEMP_FILE_FOR_WRITING = "Failed to open temp file for writing.";
    public static final String FAILED_TO_COPY_TEMP_FILE = "Failed to copy temp file to replace the storage file.";
    public static final String FAILED_TO_INIT_KEYS_LINE_NUMBERS = "Failed to init keys line numbers.";
    public static final String FAILED_TO_GET_FILE_LINES = "Failed to get lines from storage file.";
    public static final String FAILED_TO_READ_FROM_FILE = "Failed to read storage from file.";
    public static final String FAILED_TO_CREATED_STORAGE_FILE = "Failed to created storage file.";
    public static final String FAILED_TO_PUT_TO_THE_STORAGE_FILE = "Failed to put to the storage file.";

    private final String recordsFilename;
    private final Path recordsPath;

    private final Map<K, Long> keysLineNumbers;
    private long nextLineNumber;

    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    private Path tempPath;

    public FileKeyValueStorage(final String recordsFilename,
                               final Serializer<K> keySerializer,
                               final Serializer<V> valueSerializer) {

        this.recordsFilename = recordsFilename;
        this.recordsPath = Path.of(this.recordsFilename);
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.keysLineNumbers = new HashMap<>();

        if (Files.exists(this.recordsPath)) {
            initKeysLineNumbers();
        } else {
            try {
                Files.createFile(recordsPath);
            } catch (final IOException e) {
                throw new StorageException(FAILED_TO_CREATED_STORAGE_FILE, e);
            }
        }
    }

    @Override
    public V put(final K key, final V value) {
        final Long lineNumber = keysLineNumbers.get(key);
        if (lineNumber == null) {
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(recordsPath, StandardOpenOption.APPEND)) {
                bufferedWriter.write(serializeRecord(key, value));
                bufferedWriter.newLine();
            } catch (final IOException e) {
                throw new StorageException(FAILED_TO_PUT_TO_THE_STORAGE_FILE, e);
            }
            keysLineNumbers.put(key, nextLineNumber++);
            return null;
        }

        final Map<K, Long> updatedLineNumbers = new HashMap<>();
        V previousValue;
        try (BufferedReader bufferedReader = Files.newBufferedReader(recordsPath);
             BufferedWriter tempWriter = getTempWriter()) {

            final String deletedLine = deleteLine(lineNumber, updatedLineNumbers, bufferedReader, tempWriter);
            previousValue = deserializeRecord(deletedLine).value();
            tempWriter.write(serializeRecord(key, value));
            tempWriter.newLine();
        } catch (final IOException e) {
            throw new StorageException(FAILED_TO_PUT_TO_THE_STORAGE_FILE, e);
        }

        copyTemp();
        keysLineNumbers.putAll(updatedLineNumbers);
        keysLineNumbers.put(key, nextLineNumber - 1);
        return previousValue;
    }

    @Override
    public V deleteByKey(final K key) {
        final Long lineNumber = keysLineNumbers.get(key);
        if (lineNumber == null) {
            return null;
        }

        final Map<K, Long> updatedLineNumbers = new HashMap<>();
        V deletedValue;
        try (BufferedReader bufferedReader = Files.newBufferedReader(recordsPath);
             BufferedWriter bufferedWriter = getTempWriter()) {

            final String deletedLine = deleteLine(lineNumber, updatedLineNumbers, bufferedReader, bufferedWriter);
            deletedValue = deserializeRecord(deletedLine).value();
        } catch (IOException e) {
            throw new StorageException(FAILED_TO_READ_FROM_FILE, e);
        }

        copyTemp();
        keysLineNumbers.putAll(updatedLineNumbers);
        keysLineNumbers.remove(key);
        --nextLineNumber;
        return deletedValue;
    }

    @Override
    public V getByKey(final K key) {
        final Long lineNumber = keysLineNumbers.get(key);
        if (lineNumber == null) {
            return null;
        }

        final String line = safeGetLines().skip(lineNumber)
                .findFirst()
                .orElse(null);
        if (line == null) {
            return null;
        }

        final Record<K, V> record = deserializeRecord(line);
        return record.value();
    }

    @Override
    public Map<K, V> getAll() {
        return safeGetLines().map(this::deserializeRecord)
                .collect(Collectors.toMap(Record::key, Record::value));
    }

    private Stream<String> safeGetLines() {
        try {
            return Files.lines(Path.of(recordsFilename));
        } catch (final IOException e) {
            throw new StorageException(FAILED_TO_GET_FILE_LINES, e);
        }
    }

    private String serializeRecord(final K key, final V value) {
        final String serializedKey = keySerializer.serialize(key);
        final String serializedValue = valueSerializer.serialize(value);
        return serializedKey + KV_DELIM + serializedValue;
    }

    private Record<K, V> deserializeRecord(final String recordStr) {
        final String[] keyValueStrings = recordStr.split(KV_DELIM, RECORD_PARTS_COUNT);
        final K key = keySerializer.deserialize(keyValueStrings[RECORD_KEY_INDEX]);
        final V value = valueSerializer.deserialize(keyValueStrings[RECORD_VALUE_INDEX]);
        return new Record<>(key, value);
    }

    private String deleteLine(final long lineNumber,
                              final Map<K, Long> updatedLineNumbers,
                              final BufferedReader from,
                              final BufferedWriter to) throws IOException {

        long currentLineNumber = 0;
        String deletedLine = null;

        String currentLine;
        while ((currentLine = from.readLine()) != null) {
            if (currentLineNumber == lineNumber) {
                deletedLine = currentLine;
                ++currentLineNumber;
                continue;
            }

            to.append(currentLine);
            to.newLine();

            if (currentLineNumber > lineNumber) {
                updatedLineNumbers.put(deserializeRecord(currentLine).key(), currentLineNumber - 1);
            }
            ++currentLineNumber;
        }
        to.flush();

        return deletedLine;
    }

    private BufferedWriter getTempWriter() {
        if (tempPath == null) {
            try {
                tempPath = Files.createTempFile(TEMP_RECORDS_PREFIX, TEMP_RECORDS_SUFFIX);
            } catch (final IOException e) {
                throw new StorageException(FAILED_TO_CREATE_TEMP_FILE, e);
            }
        }
        try {
            return Files.newBufferedWriter(tempPath, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (final IOException e) {
            throw new StorageException(FAILED_TO_OPEN_TEMP_FILE_FOR_WRITING, e);
        }
    }

    private void copyTemp() {
        if (tempPath != null) {
            try {
                Files.copy(tempPath, recordsPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                throw new StorageException(FAILED_TO_COPY_TEMP_FILE, e);
            }
        }
    }

    private void initKeysLineNumbers() {
        try (BufferedReader bufferedReader = Files.newBufferedReader(recordsPath)) {
            long currentLineNumber = 0;

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                keysLineNumbers.put(deserializeRecord(line).key(), currentLineNumber++);
            }

            nextLineNumber = currentLineNumber;
        } catch (final IOException e) {
            throw new StorageException(FAILED_TO_INIT_KEYS_LINE_NUMBERS, e);
        }
    }

}
