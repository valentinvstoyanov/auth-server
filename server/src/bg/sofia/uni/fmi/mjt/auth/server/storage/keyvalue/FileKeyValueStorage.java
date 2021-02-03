package bg.sofia.uni.fmi.mjt.auth.server.storage.keyvalue;

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

public class FileKeyValueStorage<K, V> implements KeyValueDataStore<K, V> {

    private static final String TEMP_RECORDS_PREFIX = "temp-records";
    private static final String TEMP_RECORDS_SUFFIX = "";

    private static final String KV_DELIM = " ";
    private static final int RECORD_PARTS_COUNT = 2;
    private static final int RECORD_KEY_INDEX = 0;
    private static final int RECORD_VALUE_INDEX = 1;

    private final String recordsFilename;
    private final Path recordsPath;

    private final Map<K, Long> keysLineNumbers;
    private long nextLineNumber;

    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    private Path tempPath;

    public FileKeyValueStorage(final String recordsFilename,
                               final Serializer<K> keySerializer,
                               final Serializer<V> valueSerializer) throws IOException {

        this.recordsFilename = recordsFilename;
        this.recordsPath = Path.of(this.recordsFilename);
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.keysLineNumbers = new HashMap<>();

        if (Files.exists(this.recordsPath)) {
            initKeysLineNumbers();
        } else {
            Files.createFile(recordsPath);
        }
    }

    @Override
    public V put(final K key, final V value) throws IOException {
        final Long lineNumber = keysLineNumbers.get(key);
        if (lineNumber == null) {
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(recordsPath, StandardOpenOption.APPEND)) {
                bufferedWriter.write(serializeRecord(key, value));
                bufferedWriter.newLine();
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
        }
        copyTemp();
        keysLineNumbers.putAll(updatedLineNumbers);
        keysLineNumbers.put(key, nextLineNumber - 1);
        return previousValue;
    }

    @Override
    public V deleteByKey(final K key) throws IOException {
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
        }

        copyTemp();
        keysLineNumbers.putAll(updatedLineNumbers);
        keysLineNumbers.remove(key);
        --nextLineNumber;
        return deletedValue;
    }

    @Override
    public V getByKey(final K key) throws IOException {
        final Long lineNumber = keysLineNumbers.get(key);
        if (lineNumber == null) {
            return null;
        }

        final String line = Files.lines(Path.of(recordsFilename))
                .skip(lineNumber)
                .findFirst()
                .orElse(null);
        if (line == null) {
            return null;
        }

        final Record<K, V> record = deserializeRecord(line);
        return record.value();
    }

    @Override
    public Map<K, V> getAll() throws IOException {
        return Files.lines(Path.of(recordsFilename))
                .map(this::deserializeRecord)
                .collect(Collectors.toMap(Record::key, Record::value));
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

    private BufferedWriter getTempWriter() throws IOException {
        if (tempPath == null) {
            tempPath = Files.createTempFile(TEMP_RECORDS_PREFIX, TEMP_RECORDS_SUFFIX);
        }
        return Files.newBufferedWriter(tempPath, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void copyTemp() throws IOException {
        if (tempPath != null) {
            Files.copy(tempPath, recordsPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void initKeysLineNumbers() throws IOException {
        try (BufferedReader bufferedReader = Files.newBufferedReader(recordsPath)) {
            long currentLineNumber = 0;

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                keysLineNumbers.put(deserializeRecord(line).key(), currentLineNumber++);
            }

            nextLineNumber = currentLineNumber;
        }
    }

    private void append(final K key, final V value, final BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write(serializeRecord(key, value));
        bufferedWriter.newLine();
    }

}
