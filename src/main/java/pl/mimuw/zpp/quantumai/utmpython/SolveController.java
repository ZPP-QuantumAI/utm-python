package pl.mimuw.zpp.quantumai.utmpython;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

import static java.io.File.createTempFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SolveController {
    private final SolveService solveService;
    @GetMapping("/hello-world")
    public String test() {
        return "hello world";
    }

    @PostMapping(value = "/solve", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> solve(
            @RequestPart MultipartFile pythonFile,
            @RequestPart MultipartFile inputFile
    ) throws Exception {
        return solve(() -> tempFile(pythonFile), () -> tempFile(inputFile));
    }

    @PostMapping("/solve-string")
    public ResponseEntity<String> solve(
            @RequestParam String programText,
            @RequestParam String inputText
    ) throws Exception {
        return solve(() -> tempFile(programText), () -> tempFile(inputText));
    }

    private ResponseEntity<String> solve(
            Callable<File> pythonCallable,
            Callable<File> inputCallable
    ) throws Exception {
        File python = pythonCallable.call();
        File input = inputCallable.call();

        String result = solveService.solve(python, input);

        deleteFile(python);
        deleteFile(input);

        return ResponseEntity.ok(result);
    }

    private static File tempFile(MultipartFile multipartFile) throws IOException {
        File file = createTempFile("temp", null);
        multipartFile.transferTo(file);
        return file;
    }

    private static File tempFile(String fileContent) throws IOException {
        Path filePath = Files.createTempFile("temp", null);
        Files.write(filePath, fileContent.getBytes(), StandardOpenOption.WRITE);
        return filePath.toFile();
    }

    private static void deleteFile(File file) {
        if (!file.delete()) {
            log.warn("file {} wasn't deleted successfully", file.getAbsolutePath());
        }
    }
}
