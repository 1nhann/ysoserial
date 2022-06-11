package ysoserial.payloads.util;

import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FileUtils;

public class JavaCompiler {
    public static byte[] compile(String className,String javacode) throws Exception{

        String tmpPath = Files.createTempDirectory("1nhann").toFile().getPath();

        String path = tmpPath + File.separator + className + ".java";

        javax.tools.JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardJavaFileManager = javaCompiler
            .getStandardFileManager(null,null,null);

        Files.write(Paths.get(path), javacode.getBytes(StandardCharsets.UTF_8));
        File file = new File(path);
        Iterable fileObject = standardJavaFileManager.getJavaFileObjects(file);
        ArrayList options = new ArrayList<String>(1);
        options.add("-nowarn");
        javaCompiler.getTask(null, standardJavaFileManager,null, options, null, fileObject).call();

        byte[] bytes = Files.readAllBytes(Paths.get(tmpPath + File.separator + className + ".class"));

        FileUtils.cleanDirectory(new File(tmpPath));
        Files.delete(Paths.get(tmpPath));

        return bytes;
    }
}
