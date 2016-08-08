/**
 * © Copyright IBM Corporation 2016.  
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp. 
 */
 
import com.urbancode.air.AirPluginTool
import com.urbancode.air.CommandHelper
import groovy.json.JsonBuilder

import com.plugins.RestHelper

final airTool = new AirPluginTool(args[0], args[1])
final def props = airTool.getStepProperties()

// Retrieve Properties
def github        = props['gitHubUrl'].trim()
def username      = props['username'].trim()
def password      = props['password']
def organization  = props['organization'].trim()
def names         = props['names'].split("\n|,")*.trim() // Create Repositories
def description   = props['description'].trim()
def homepage      = props['homepage'].trim()
def privateRepo   = props['privateRepo'].toBoolean()
def has_issues    = props['has_issues'].toBoolean()
def has_wiki      = props['has_wiki'].toBoolean()
def has_downloads = props['has_downloads'].toBoolean()
def team_id       = props['team_id'].trim()
def auto_init     = props['auto_init'].toBoolean()
def gitignore_template = props['gitignore_template'].trim()
def license_template   = props['license_template'].trim()

try {
    // Create GitHub REST URL
    // Public: https://api.github.com/...
    // Enterprise: https://[hostname]/api/v3/...
    
    def fullURL = ""
    final def enterprisePath ="/api/v3" //Only needed for enterprise
    final def publicGitHub = "https://api.github.com"
    
    if (!github || (publicGitHub == github)) {
        github = publicGitHub
    }
    else {
        github = github + enterprisePath
    }
    
    // User Path Example: "https://api.github.com/user/repos"
    // Organization Path Example: "https://api.github.com/orgs/${org}/repos"
    if (organization){
        fullURL = github + "/orgs/${organization}/repos"
    }
    else {
        fullURL = github + "/user/repos"
    }
    
    println "GitHub URL: '${fullURL}'"
    
    // Create REST  Client
    
    for (name in names) {
        println "======================================================================="
        println "[Action] Creating '${name}' Repository..."
        // Create New Repository Payload
        // https://developer.github.com/v3/repos/#create
        def newRepo = [:]
        newRepo.put("name", name)
        newRepo.put("description", description)
        newRepo.put("homepage", homepage)
        newRepo.put("private", privateRepo)
        newRepo.put("has_issues", has_issues)
        newRepo.put("has_wiki", has_wiki)
        newRepo.put("has_downloads", has_downloads)
        if (organization && team_id) {
            newRepo.put("team_id", team_id.toInteger())
        }
        newRepo.put("auto_init", auto_init)
        newRepo.put("gitignore_template", gitignore_template)
        newRepo.put("license_template", license_template)
        
        JsonBuilder payload = new JsonBuilder(newRepo)
        println payload.toString()
        
        // Build and Run REST Call
        RestHelper client = new RestHelper(username, password)
        def clone_url = client.createRepository(fullURL, payload)
        println "[Ok] The '${name}' repository has been successfully created!"
        
        airTool.setOutputProperty(name, clone_url);
        airTool.storeOutputProperties();
    }
}
catch (Exception ex){
    println "[Error] The Create Repository step failed!"
    ex.printStackTrace()
    System.exit(1)
}
println "[Ok] All repositories have been successfully created!"