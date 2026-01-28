import threading

class MyThreadClassInp(threading.Thread): 
    def __init__(self, a):
        super(MyThreadClassInp, self).__init__() 
        self.a = a
        
    def run(self):
        c = 0
        while (c < self.a):
            c = c + 1

def f(a):
    c = 0
    while (c < a):
        c = c + 1
        print("Hello Func", flush=True)


myThreadFunc = threading.Thread(target=f, args=(100,)) 
myThreadObj = MyThreadClassInp(100) 

myThreadFunc.start() 
myThreadObj.start() 

myThreadFunc.join() 
myThreadObj.join()
