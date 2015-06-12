package xcorexview.metrics.classes;
import java.util.Scanner;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import xmetamodel.XClass;

import com.salexandru.xcorex.interfaces.IPropertyComputer;
import com.salexandru.xcorex.metaAnnotation.PropertyComputer;


@PropertyComputer
public class NumberOfLines implements IPropertyComputer<Integer, XClass> {
	@Override
	public Integer compute(XClass entity) {
		final IType unit = entity.getUnderlyingObject();
		
		try {
			final String commentsRegex = "(?://.*)|(/\\*(?:.|[\\n\\r])*?\\*/)";
			
			final String sourceCode = unit.getCompilationUnit().getSource().replaceAll(commentsRegex, "");
			
			int count = 0;
			for (final Scanner scanner = new Scanner(sourceCode); scanner.hasNext(); ) {
				final String line = scanner.nextLine();
				if (!line.trim().isEmpty()) {
					++count;
				}
			}
			
			
			return count;
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
}
