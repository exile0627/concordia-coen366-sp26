#include <iostream>
#include <future>
#include <chrono>

int long_processing() {
    std::this_thread::sleep_for(std::chrono::seconds(1)); // Processing
    return 42;
}

int main() {
    // Launch task asynchronously
    std::future<int> result_future = std::async(std::launch::async, long_processing);

    std::cout << "Main: Doing other work..." << std::endl;

    // get() blocks until the value is ready
    int result = result_future.get(); 
    std::cout << "Main: Result is " << result << std::endl;

    return 0;
}
