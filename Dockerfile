# Use the official Tomcat 9 image as the base image
FROM tomcat:9

# Remove the default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from the local filesystem into the webapps directory of Tomcat
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose the port on which your application will run (default is 8080)
EXPOSE 8080

# Start Tomcat when the container starts
CMD ["catalina.sh", "run"]
~                                                                                                                                                                                            
~                                    
