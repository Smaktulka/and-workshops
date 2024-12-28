# App Instruction

This app is the example of hot-reloading code using the custom classLoader

### Manual Run

1. Build program (Ctrl + F9)
2. Run program
3. During runtime change static method 'play' of App inner User class
   (ex. comment playFootball and uncomment playBasketball)
4. Rebuild program. Try to catch the timeout (10 seconds) after program print out,
   to not catch the Exception