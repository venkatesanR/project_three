# project_three
where business meets technology

project architecture
 		common--parent
 			--add modules to common parent
 		project--parent
 		 	--add modules to common parent

 		training-project--individual project

 		documents 	
 		 --functional-documents
 		 --tectnical-documents	


 <!-- Dont Execute JUST FOR KNOWING -->
creating parent pom
mvn archetype:generate -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeArtifactId=pom-root -DarchetypeVersion=RELEASE

mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=RELEASE