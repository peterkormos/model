SET dataFile=d:\Download\2012MosonElonevezett.gzip

set basedir=d:\kp\java\Modelregistration\WebContent\WEB-INF\classes
java -cp %basedir%; servlet.ListData %dataFile%

pause