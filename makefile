all: main
	java -cp bin Main

main:
	javac -d bin src/*
