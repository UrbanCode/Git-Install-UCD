/**
 * © Copyright IBM Corporation 2016.  
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp. 
 */
 
 package com.plugins
 
 import java.lang.StringBuilder
 
 public class CLIHelper {
 
    /*
     * @param url The full URL to acecss the repository
     *
     * @return String determining whether it is a ssh or https URL
     */
    public static String checkExternalRepoURL(String url) {
        if (!url.startsWith("https://") && !url.startsWith("ssh://")) {
            println "[Error] External Repository URL '${url} must begin with 'ssh://' or 'https://'."
            System.exit(1)
        }
        return url.substring(0, url.indexOf(":"))
    }
    
    /*
     * @param url The full URL to acecss the repository
     *
     * @return String determining whether it is a valid GitHub https:// url
     */
    public static void checkHTTPSRepoURL(String url) {
        if (!url.startsWith("https://")) {
            println "[Error] The HTTPS Repository URL '${url} must begin with 'https://'."
            System.exit(1)
        }
    }

    /*
     * @param url The full clone URL to access the repository
     *
     * @return The name of the respository 
     */
    public static String getRepositoryName(String url) {
        String repo = url.substring(url.lastIndexOf("/") + 1)
        
        if (repo.endsWith(".git")) {
            repo = repo.substring(0, repo.lastIndexOf(".git"))
        }
        return repo
    }

    /*
     * @param url The full URL to acecss the repository
     * @param username The username to add to the repository URL
     * @param password The password to add to the repository URL
     *
     * @return String clone HTTPS url with the credentials inserted
     */
    public static String addCredentials2URL(String url, String username, String password) {
        checkHTTPSRepoURL(url)
        String credentials = "${username}:${password}@"
        StringBuilder builder = new StringBuilder(url)
        builder.insert(8, credentials)
        return builder.toString()
    }
 }