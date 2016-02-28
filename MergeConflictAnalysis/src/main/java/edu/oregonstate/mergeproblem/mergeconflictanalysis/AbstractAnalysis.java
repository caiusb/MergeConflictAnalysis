package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.Main;

import java.io.BufferedOutputStream;

public abstract class AbstractAnalysis {
    abstract public void doAnalysis(Main.Config config, BufferedOutputStream outputStream) throws Exception;
}
