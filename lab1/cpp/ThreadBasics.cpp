#include <iostream>
#include <thread>

void hello() {
    int c = 0;
    while (c < 100) {
        c = c + 1;
        std::cout << "Hello Thread 1" << std::endl;
    }
}

void helloInp(int a) {
    int c = 0;
    while (c < a) {
        c = c + 1;
        // std::endl 负责在控制台换行并立即“刷新缓冲区”，确保日志即时显示
        std::cout << "Hello Thread 2" << std::endl; 
    }
}
 

int main() {
    std::thread th(hello);
    std::thread th2(helloInp, 100);
    th.join();
    th2.join();

    std::cout << "Main thread finished" << std::endl;
    return 0;
}
