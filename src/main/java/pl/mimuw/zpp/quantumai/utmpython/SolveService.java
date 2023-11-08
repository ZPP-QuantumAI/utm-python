package pl.mimuw.zpp.quantumai.utmpython;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import static java.io.File.createTempFile;

@Service
@Slf4j
public class SolveService {

    public String solve(File pythonFile, File inputFile) throws IOException, InterruptedException {
        try (GenericContainer<?> container = new GenericContainer<>("python:latest")
                .withCommand("sh", "-c", "python3 /mnt/user_code.py < /mnt/input.txt > /mnt/output.txt")
                .withCopyFileToContainer(MountableFile.forHostPath(pythonFile.getAbsolutePath()), "/mnt/user_code.py")
                .withCopyFileToContainer(MountableFile.forHostPath(inputFile.getAbsolutePath()), "/mnt/input.txt")
                .waitingFor(Wait.forLogMessage(".*Container started.*", 1))
                .withStartupTimeout(Duration.ofSeconds(30))) {
            container.start();

            // Your code to interact with the container
            // (You can add any additional interactions with the container here if needed)

            // Wait for the container to finish
            container.followOutput(outputFrame -> {
                // Handle the output frame if necessary
            });

            // After the container has run, copy the output file from the container to a local file
            File outputFile = new File("path_to_local_output.txt");
            // container.copyFileFromContainer("/mnt/output.txt", MountableFile.forHostPath(outputFile.getAbsolutePath()));

            return Files.readString(outputFile.toPath());
        } catch (Exception e) {
            log.error("Error while running the Python script in a container: {}", e.getMessage());
            throw e;
        }
    }



    private static void deleteFile(File file) {
        if (!file.delete()) {
            log.warn("file {} wasn't deleted successfully", file.getAbsolutePath());
        }
    }
}
