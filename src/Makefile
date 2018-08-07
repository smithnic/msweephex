SRCS = ./*/*.java ./*/*/*.java
RM = rm -f 
FINDCLEAN = find . -name "*.class" -type f -delete

all: dirs classes dist

dirs:
	mkdir -p ../dist
	echo "Main-Class: msweep.Msweep" > ./manifest.txt 

classes: dirs
	javac $(SRCS)

dist: classes 
	jar cmf ./manifest.txt ../dist/MsweepHex.jar ./* 

clean:
	$(RM) ./manifest.txt
	$(FINDCLEAN)
	$(RM) -r ../dist

.PHONY: clean


