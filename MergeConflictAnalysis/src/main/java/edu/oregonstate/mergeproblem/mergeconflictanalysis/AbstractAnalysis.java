package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.Main;

import java.io.BufferedOutputStream;

/**
 * Created by caius on 2/27/16.
 */
public abstract class AbstractAnalysis {
    abstract void doAnalysis(Main.Config config, BufferedOutputStream outputStream) throws Exception;
}
