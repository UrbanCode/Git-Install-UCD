/**
 * © Copyright IBM Corporation 2016.  
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp. 
 */
 
import com.urbancode.air.AirPluginTool
import com.urbancode.air.CommandHelper

import com.plugins.CLIHelper

final airTool = new AirPluginTool(args[0], args[1])
final def props = airTool.getStepProperties()

// Retrieve Properties
def extUrl          = props['extUrl'].trim()
def extUsername     = props['extUsername'].trim()
def extPassword     = props['extPassword']
def ghUrl           = props['ghUrl'].trim()
def ghUsername      = props['ghUsername'].trim()
def ghPassword      = props['ghPassword']

try {

    CommandHelper ch = new CommandHelper(new File("."))
    
    def cloneType    = CLIHelper.checkExternalRepoURL(extUrl)
    def extRepo      = CLIHelper.getRepositoryName(extUrl)
    if (cloneType == "https") {
        println "[Ok] HTTPS repository found."
        extUrl = CLIHelper.addCredentials2URL(extUrl, extUsername, extPassword)
    }
    else if (cloneType == "ssh") {
        println "[Ok] SSH repository found."
        println "[Warning] SSH requires Agent to be logged in prior to the Migrate Repository step running."
    }
    else {
        println "[Error] This step currently only support https and ssh repositories."
        System.exit(1)
    }

    CLIHelper.checkHTTPSRepoURL(ghUrl)
    ghUrl = CLIHelper.addCredentials2URL(ghUrl, ghUsername, ghPassword)
    
    // GitHub Documentation: Importing a Git repository using the command line
    // https://help.github.com/articles/importing-a-git-repository-using-the-command-line/
    File script = new File("script.sh")
    script.deleteOnExit()
    script << "#!/bin/sh\n"
    script << "set -e\n"
    script << "git clone ${extUrl}\n"
    script << "cd ${extRepo}*\n"
    script << "git push --mirror ${ghUrl}\n"
    script << "cd ..\n"
    script << "rm -rf ${extRepo}*\n"
    
    println "[Ok] Generated Script..."
    println script.text
    
    def args = ["sh", "-c", "./script.sh"]

    ch.runCommand("[Action] Migrating Repository..." , args)
}
catch (Exception ex){
    println "[Error] Repository failed to migrate!"
    ex.printStackTrace()
    System.exit(1)
}
println "[Ok] Repository successfully migrated!"