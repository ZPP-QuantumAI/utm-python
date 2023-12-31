package pl.mimuw.zpp.quantumai.utmpython;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.io.File.createTempFile;

@Service
@Slf4j
public class SolveService {
    public String solve(
            File pythonFile,
            File inputFile
    ) throws IOException, InterruptedException {
        String pythonFileAbsolutePath = pythonFile.getAbsolutePath();
        File outputFile = createTempFile("output", "txt");
        ProcessBuilder processBuilder = new ProcessBuilder("python3", pythonFileAbsolutePath)
                .redirectInput(inputFile)
                .redirectOutput(outputFile);
        Process process = processBuilder.start();
        process.waitFor();
        String result = Files.readString(Path.of(outputFile.getAbsolutePath()));
        deleteFile(outputFile);
        return result;
    }

    private static void deleteFile(File file) {
        if (!file.delete()) {
            log.warn("file {} wasn't deleted successfully", file.getAbsolutePath());
        }
    }
}
