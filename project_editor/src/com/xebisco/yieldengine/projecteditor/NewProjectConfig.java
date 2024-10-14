package com.xebisco.yieldengine.projecteditor;

import com.xebisco.yieldengine.uiutils.fields.annotations.ComboStrings;
import com.xebisco.yieldengine.uiutils.fields.annotations.Config;
import com.xebisco.yieldengine.uiutils.fields.annotations.Visible;

@Config(title = "New Project")
public class NewProjectConfig {
    @Visible
    public String name;
    @Visible
    @ComboStrings(values = {"", "Hello, World!"})
    public String template = "";
    @Visible
    public boolean createRepository;
}
