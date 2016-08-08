/**
 * © Copyright IBM Corporation 2016.  
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

package com.plugins

import groovy.json.JsonSlurper
import org.apache.commons.codec.binary.Base64
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.auth.AuthScope
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils;

public class RestHelper {

    CloseableHttpClient client
    HttpGet httpget
    HttpPost httppost
    String credentials
    String URL

    public RestHelper() {
        client = HttpClients.createDefault()

    }
    
    /*
     * @param username The username to access the repository
     * @param password The password to access the repository
     */
    public RestHelper(def username, def password) {
        client = HttpClients.createDefault()
        String userPass = username + ":" + password
        byte[] credentialsBytes = Base64.encodeBase64(userPass.getBytes());
        credentials = new String(credentialsBytes)
    }

    /*
     * @param URL The full URL needed to complete the POST REST call
     * @param payload The JSON data to send with the POST REST call
     *
     * @return The HttpResponse containing the POST call's response
     */
    private HttpResponse doPost(def URL, def payload) {
        httppost = new HttpPost(URL)
        httppost.setHeader("Content-Type", "application/json")
        httppost.setHeader("Accept", "application/json")
        httppost.setHeader("Authorization", "BASIC ${credentials}")

        StringEntity entity = new StringEntity(payload.toString(), "UTF-8")
        entity.setContentType("application/json")
        httppost.setEntity(entity)
        
        return client.execute(httppost)
        
    }
    
    /*
     * @param URL The full URL used to create the repository
     * @param payload The JSON data with the initial repository data
     *
     * @return The clone_url retrieved from the created repository HttpResponse
     */
    public String createRepository(def URL, def payload) {
        HttpResponse response = doPost(URL, payload)
        def statusLine = response.getStatusLine()
        println "[Output] ${statusLine}"
        def exitCode = statusLine.getStatusCode()
        if (exitCode != 201) {
            println "[Error] Create Repository failed with the following error code: '${exitCode}'."
            System.exit(1)
        }

        def slurper = new JsonSlurper()
        def result = slurper.parseText(EntityUtils.toString(response.getEntity()))
        return result.clone_url
    }
}