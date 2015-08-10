//
// Generated from archetype; please customize.
//

package ru.crimea

import org.codehaus.gmaven.mojo.GroovyMojo
import ru.crimea.builder.config.ClassConfigurationContext

/**
 * Echos an object string to the output screen.
 * @goal echo
 * @requiresProject false
 */
class ContentBuilder extends GroovyMojo{
    /**
     * @parameter expression="${message}" default-value="Hello World"
     */
    private List<String> modules;

    /**
     * @parameter default-value="ru.intertrust.generated"
     */
    private String packagePrefix;

    /**
     * @parameter default-value="generated"
     */
    private String outPutFolder;

    void execute() {
        println "Start generate classes";

        if(modules == null || modules.isEmpty()){
            println "No one modules were added";
            return;
        }
        try{
            ClassConfigurationContext context = new ClassConfigurationContext();
            for(String module : modules){
                println "start load module" + module;
                loadModuleToContext(context, module);
                println "finish load module" + module;
            }

            context.buildContext(packagePrefix, new File(outPutFolder));
        }catch (Exception e){
            cleanData()
            throw new RuntimeException("Error in generation source code", e);
        }
    }

    private void loadModuleToContext( ClassConfigurationContext context, String modulePath){


    }

    private void cleanData(){
       new File(outPutFolder).deleteDir();
    }
}
