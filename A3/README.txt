------oO INSTALLATION GUIDE Oo--------

Group Members:

  - Jacob Boyce: c3264527
  - Wei Chen: c3355372
  - Akshata Dhuraji: c3309266

PLEASE READ EVERYTHING CAREFULLY!!!!

Note we have already provided a built artifact folder called "c3264527_c3309266_c3355372_FinalProject". The resulting compilation guide below will produce a folder called "build" which can be renamed if neccesary!

Compilation:

  Once again the assignment provided no compilation details so we made an APACHE ANT configuration for you to compile the project with.
  You may view the file 'build.xml' to verify the build configuration. 

  Using this folder 

  1) run the comamnd 'ant clean'
  2) run the command 'ant compile'
  3) the newly created artifact will be the folder 'build'

Note - you can follow the installation guide below using the folder "build" instead of "c3264527_c3309266_c3355372_FinalProject" if you chose to compile. 

Installation:

  1) Copy paste '/c3264527_c3309266_c3355372_FinalProject' into '/{tomcat-dir}/webapps/'
  2) Setup Microsoft SQL server (Azure) using localhost/SQLEXPRESS , Port 1433. The context.xml located in the 'META-INF' folder provides more details on the setup.
  3) Run the SQL script 'SQLQuery.sql' on a setup Microsoft MySQL server connection.
  4) Go to the /{tomcat-dir}/bin folder and run catalina.sh
  5) On your favourite web browser go to http://localhost:8080/c3264527_c3309266_c3355372_FinalProject/

Note - If you get some issue like Fatal Database Error copy over the jars found in WEB-INF/lib to /{tomcat-dir}/lib.

Logins:
  Users:
    - Username: 'Akshi' Password: 'Akshi'
    - Username: 'Reena' Password: 'Reena'
    - Username: 'Sam' Password: 'Sam'

  IT Staff
    - Username: 'Nova' Password: 'Nova'
    - Username: 'Mita' Password: 'Mita'

Additional Requirements:

10. “The categories are very broad and could congest the Knowledge-Base. Can we have some sub-
categories as well (Appendix 6.2)” – IT staff. (weight 20)

16. “We should be able to view Knowledge-Base articles sorted by their categories” – User. (weight 5) 

17. “We should be able to sort the issues by the date that they were reported” – IT staff. (weight 5)

Source Code:

  You may view source in the folder /WEB-INF/classes