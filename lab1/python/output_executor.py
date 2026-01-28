from concurrent.futures import ThreadPoolExecutor

# Define the task as a simple function instead of a class method
def factorial_task(a):
    """Logic originally from MyThreadClassInpOutp.run"""
    print("Hello Object")
    c = 1
    result = 1
    while(c < (a + 1)):
        result = result * c
        c = c + 1
    return result # Directly return the result

# Using the 'with' statement ensures the executor shuts down automatically
with ThreadPoolExecutor(max_workers=1) as executor:
    # Submit the task with an input value of 10
    # This is similar to 'service.submit(task)' in Java
    future = executor.submit(factorial_task, 10)
    
    print("Task submitted, waiting for result...")
    
    # future.result() blocks until the thread finishes and returns the value
    # This is equivalent to 'future.get()' in Java 
    # and 'the_answer.get()' in C++
    final_b = future.result() 

print(f"value of b after: {final_b}")
