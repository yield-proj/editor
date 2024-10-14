package com.xebisco.yieldengine.projecteditor;

import com.xebisco.yieldengine.jarmng.JarMng;
import com.xebisco.yieldengine.project.Project;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Workspace implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static Workspace create(File dir) {
        //noinspection ResultOfMethodCallIgnored
        dir.mkdir();
        Workspace workspace = new Workspace();
        for (File dir1 : Objects.requireNonNull(dir.listFiles())) {
            if (dir1.isDirectory()) {
                for (File file : Objects.requireNonNull(dir1.listFiles())) {
                    if (file.getName().equals("project.serp")) {
                        try (ObjectInputStream oi = new ObjectInputStream(Files.newInputStream(file.toPath()))) {
                            /*projectJar.invokeMethod(project, "setLastModified", Object.class, new Class<?>[]{Date.class}, new Object[]{Date.from(Files.getLastModifiedTime(dir1.toPath()).toInstant())});
                            projectJar.invokeMethod(project, "setDirectory", Object.class, new Class<?>[]{File.class}, new Object[]{dir1});
                            projectJar.invokeMethod(project, "setCompatible", Object.class, new Class<?>[]{boolean.class}, new Object[]{true});*/

                            workspace.getProjects().add(((Project) oi.readObject()).setLastModified(Date.from(Files.getLastModifiedTime(dir1.toPath()).toInstant())).setDirectory(dir1).setCompatible(true));
                            break;
                        } catch (IOException | ClassNotFoundException e) {
                            //TODO
                            /*Object project = PROJECT_JAR.getClassForName("com.xebisco.yieldengine.project.Project");
                            PROJECT_JAR.invokeMethod(project, "setLastModified", Object.class, new Class<?>[]{Date.class}, new Object[]{Date.from(Files.getLastModifiedTime(dir1.toPath()).toInstant())});
                            PROJECT_JAR.invokeMethod(project, "setDirectory", Object.class, new Class<?>[]{File.class}, new Object[]{dir1});
                            PROJECT_JAR.invokeMethod(project, "setCompatible", Object.class, new Class<?>[]{boolean.class}, new Object[]{true});*/
                            workspace.getProjects().add(new Project().setName(dir1.getName()).setDirectory(dir1).setLastModified(new Date(0)).setCompatible(false));
                            //Utils.showError(e.getMessage(), null);
                        }
                    }
                }
            }
        }
        //Collections.sort(workspace.getProjects());
        return workspace;
    }

    private final ArrayList<Project> projects = new ArrayList<>();

    public ArrayList<Project> getProjects() {
        return projects;
    }
}
