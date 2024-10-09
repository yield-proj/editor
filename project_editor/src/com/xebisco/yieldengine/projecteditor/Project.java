package com.xebisco.yieldengine.projecteditor;

import com.xebisco.yieldengine.uiutils.fields.Editable;
import com.xebisco.yieldengine.uiutils.fields.Visible;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class Project implements Serializable, Comparable<Project> {
    @Visible
    @Editable
    private String name, description, author;

    private transient boolean compatible = true;
    private transient Date lastModified;
    private transient File directory;

    public String getName() {
        return name;
    }

    public Project setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Project setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Project setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Project setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    @Override
    public int compareTo(Project o) {
        if (o.lastModified != null && lastModified != null)
            return o.lastModified.compareTo(this.lastModified);
        return 0;
    }

    public File getDirectory() {
        return directory;
    }

    public Project setDirectory(File directory) {
        this.directory = directory;
        return this;
    }

    public boolean isCompatible() {
        return compatible;
    }

    public Project setCompatible(boolean compatible) {
        this.compatible = compatible;
        return this;
    }
}
