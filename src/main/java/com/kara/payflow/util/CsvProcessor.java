package com.kara.payflow.util;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CsvProcessor {

    private CsvProcessor() {}

    public static <T> List<T> read(String path, Class<T> clazz) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        return mapper.readerFor(clazz)
                .with(schema)
                .<T>readValues(new File(path))
                .readAll();
    }
}
