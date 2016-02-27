package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.List;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ASTDiff;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ChunkOwner;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import fr.labri.gumtree.actions.model.Action;
import fr.labri.gumtree.actions.model.Addition;
import fr.labri.gumtree.actions.model.Delete;
import fr.labri.gumtree.actions.model.Update;

public class OperationCounterProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "NO_ADD,NO_UPDATE,NO_DELETE";
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		int noAdd = 0;
		int noUpdate = 0;
		int noDelete = 0;
		
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		String aVersion = combinedFile.getVersion(ChunkOwner.A);
		String bVersion = combinedFile.getVersion(ChunkOwner.B);
		
		List<Action> actions = new ASTDiff().getActions(aVersion, bVersion);
		for (Action action : actions) {
			if (action instanceof Addition)
				noAdd++;
			if (action instanceof Update)
				noUpdate++;
			if (action instanceof Delete)
				noDelete++;
		}
		
		return noAdd + "," + noUpdate + "," + noDelete;
	}
	
	

}
