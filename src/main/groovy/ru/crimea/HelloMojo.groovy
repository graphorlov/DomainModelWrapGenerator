//
// Generated from archetype; please customize.
//

package ru.crimea

import org.codehaus.gmaven.mojo.GroovyMojo

/**
 * Example Maven2 Groovy Mojo.
 *
 * @goal hello
 */
class HelloMojo
    extends GroovyMojo
{
    /**
     * The hello message to display.
     *
     * @parameter expression="${message}" default-value="Hello World"
     */
    String message

    void execute() {
        println "${message}"
    }
}
