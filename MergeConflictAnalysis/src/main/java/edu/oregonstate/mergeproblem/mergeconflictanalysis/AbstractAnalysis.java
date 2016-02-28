package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.BufferedOutputStream;

public abstract class AbstractAnalysis {
    abstract public void doAnalysis(Main.Config config, BufferedOutputStream outputStream) throws Exception;
}
