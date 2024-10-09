package com.xebisco.yieldengine.projecteditor;

import com.xebisco.yieldengine.uiutils.Utils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class Workspace implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static Workspace create(File dir) {
        //noinspection ResultOfMethodCallIgnored
        dir.mkdir();
        Workspace workspace = new Workspace();
        for(File dir1 : Objects.requireNonNull(dir.listFiles())) {
            if(dir1.isDirectory()) {
                for (File file : Objects.requireNonNull(dir1.listFiles())) {
                    if(file.getName().equals("project.serp")) {
                        try(ObjectInputStream oi = new ObjectInputStream(Files.newInputStream(file.toPath()))) {
                            workspace.getProjects().add(((Project) oi.readObject()).setLastModified(Date.from(Files.getLastModifiedTime(dir1.toPath()).toInstant())).setDirectory(dir1).setCompatible(true));
                            break;
                        } catch (IOException | ClassNotFoundException e) {
                            workspace.getProjects().add(new Project().setName(dir1.getName()).setDirectory(dir1).setLastModified(new Date(0)).setCompatible(false));
                            //Utils.showError(e.getMessage(), null);
                        }
                    }
                }
            }
        }
        workspace.getProjects().sort(Comparator.naturalOrder());
        return workspace;
    }

    private final ArrayList<Project> projects = new ArrayList<>();

    public ArrayList<Project> getProjects() {
        return projects;
    }
}
