git pull

ps aux | grep teachme | grep java | grep -v grep | awk -F " " '{print $2}' | xargs kill -9

JAVA_TOOL_OPTION="-Dfile.encoding=UTF-8  -XX:MaxPermSize=50m -XX:PermSize=50m -Xmx200m  -XX:HeapDumpPath=/root/hprof -XX:+HeapDumpOnOutOfMemoryError"

echo $JAVA_TOOL_OPTION
export JAVA_TOOL_OPTION

mvn clean install exec:java teachme >>log.log 2>&1 &

echo $! > process.pid
