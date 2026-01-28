#include <iostream>
#include <thread>

void helloInp(int a) {
    int c = 0;
    while (c < a) {
        c = c + 1;
        std::cout << "Hello Thread Func" << std::endl; 
    }
}

class ThreadObject {
public:
    void operator()(int a) {
        for (int i = 0; i < 100; i++) {
            std::cout << "Hello Thread CallableObjc" << std::endl;
        }
    }
};

int main() {
    std::thread th(helloInp, 100);
    std::thread tho(ThreadObject(), 10);
    
    th.join();
    tho.join();

    return 0;
}
