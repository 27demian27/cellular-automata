package com.demian.controller;

import com.demian.model.Plane;
import lombok.Getter;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;

public class SaveController {

    @Getter
    private static int saveFileNameUID;

    private final static Path savesDir = Path.of("src/main/resources/saves");

    private final Plane model;


    public SaveController(Plane model) {
        this.model = model;
        setSaveFileNameUID();
    }

    private void setSaveFileNameUID() {
        File[] saveFiles = savesDir.toFile().listFiles();

        if (saveFiles == null) {
            saveFileNameUID = 0;
            return;
        }

        for (File file : saveFiles) {
            if (file.getName().matches("\\w*\\d*.stt")) saveFileNameUID ++;
        }
    }

    public void saveState() throws IOException {
        Path saveFile = savesDir.resolve("save"+saveFileNameUID+".stt");
        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveFile.toFile()))) {
            outputStream.write(model.getSizeX());
            outputStream.write(model.getSizeY());

            model.forEachCell(c -> {
                try {
                    outputStream.write(c.state);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        saveFileNameUID++;
    }

    public void loadState(String fileName) throws IOException {
        Path saveFile = savesDir.resolve(fileName);
        if (!saveFile.toFile().isFile())
            throw new FileNotFoundException();

        try(InputStream inputStream = new BufferedInputStream(new FileInputStream(saveFile.toFile()))) {
            int sizeX = inputStream.read();
            int sizeY = inputStream.read();

            if (sizeX != model.getSizeX() || sizeY != model.getSizeY())
                model.initialize(sizeX, sizeY);

            model.forEachCell(c -> {
                try {
                    model.setState(inputStream.read(), c.getX(), c.getY());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
