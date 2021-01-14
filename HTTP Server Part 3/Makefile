port = 4644

all: compile3 

compile3: HTTP3Server.java Client.java
	javac HTTP3Server.java Client.java

project2: compile doc scripts

compile: HTTP1Server.java Client.java
	javac HTTP1Server.java Client.java

doc: doc_root.tar-1-1.gz
	tar -zxvf doc_root.tar-1-1.gz

scripts: cgi_bin.zip
	unzip cgi_bin.zip

format: doc_root cgi_bin
	mv cgi_bin doc_root

copy1: HTTP1Server.class
	mv HTTP1Server.class ./doc_root

copy2: client_handler.class
	mv client_handler.class ./doc_root

clean2:
	rm -r doc_root Client.class cgi_bin client_handler.class HTTP1Server.class

clean:
	rm -r Client.class client_handler.class HTTP3Server.class ./CookieTester

server2:
	java -cp . HTTP1Server $(port)

server:
	java -cp . HTTP3Server $(port)

tester2:
	java -jar HTTPServerTester.jar localhost $(port)

tester: 
	tar xvf CookieTester.tgz && cd CookieTester && java -jar CookieTest.jar localhost $(port)

CookieTester:
	tar -zxvf CookieTester.tgz

client:
	java Client $(port)

old: compile doc copy1 copy2

oldtester:
	java -jar HTTPServerTester-1-1.jar localhost $(port)