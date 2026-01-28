import threading

class MyThreadClassInpOutp(threading.Thread):
    def __init__(self, a):
        # Initialize the base Thread class
        super(MyThreadClassInpOutp, self).__init__()
        self.a = a  # Input parameter stored in the instance
        self.b = 0  # Initialize a state variable to hold the output/result

    def run(self):
        # Logic executed when the thread starts
        c = 1
        self.b = 1  # Resetting result variable for calculation
        while(c < (self.a + 1)):
            self.b = self.b * c  # Performing a factorial-like calculation
            c = c + 1
            print("Hello Object")

# Instantiate thread with an input value of 10
myThreadObj = MyThreadClassInpOutp(10)

print("value of b before")
print(myThreadObj.b)  # Should show 0

myThreadObj.start()   # Start thread execution
myThreadObj.join()    # Wait for thread to finish to ensure 'b' is updated

print("value of b after")
print(myThreadObj.b)  # Shows the result of the thread's calculation
