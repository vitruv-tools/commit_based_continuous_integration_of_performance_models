package cipm.consistency.models

import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator
import org.eclipse.xtend.lib.annotations.Accessors
import cipm.consistency.models.ModelDirLayout

@Accessors
class ModelDirLayoutImpl implements ModelDirLayout {
	protected Path rootDirPath

	override void initialize(Path rootDirPath) {
		this.rootDirPath = rootDirPath;
		if (!rootDirPath.toFile().exists()) {
			Files.createDirectories(rootDirPath);
		}
	}

	override delete() {
		Files.walk(rootDirPath).sorted(Comparator.reverseOrder()).forEach[
			it.toFile.delete
		]
	}
	
	def clean() {
		delete()
		initialize(rootDirPath)
	}
}
