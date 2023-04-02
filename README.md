# SENG2050
University of Newcastle - Web Technologies - 2022

SENG2050 or web technologies in a course in the University of Newcastle that focuses on building web applications using Apache Tomcatt. Tomcatt uses the Servlet's which are a server-side web technology that handle an incoming request and use it to produce a dynamic page. Tomcatt is naturally suited towards the MVC archiecture as the individual servlets act as controllers, and the JSP's work as views. All webpage rendering is done on the server-side. 

## Structure

- A1: Very simple messaging board which allows multiple users to send messages across several message boards.
- A2: A game of "avoid the secrete number" where users cannot guess the number
- A3: Simple ticketing application where users can submit or respond to IT tickets. 

## Building

Once again the assignment provided no compilation details so I made an APACHE ANT configuration for you to compile the project with.
You may view the file 'build.xml' to verify the build configuration. 

You should navigate into the individual folders to build. 

```sh
ant clean
clean build
 ```

The output should be located in the `build` folder. 

NOTE - **Additional build instructions might be found in the individual assignments `README.txt` folder**

## Installation

All programs are made for Apache Tomcat 9.0.

  1) Copy paste `/c3264527_assignment2` into `/{tomcat-dir}/webapps/`
  2) Go to the `/{tomcat-dir}/bin` folder and run `catalina.sh`
  3) On your favourite web browser go to `http://localhost:8080/c3264527_assignment2/`

URL: `http://localhost:8080/c3264527_assignment2/`
