use std::fs;
use std::path::Path;

/// Reads the content of a file and returns it as a `String`.
pub fn read_file(filename: &str) -> String {
    fs::read_to_string(Path::new(filename))
        .unwrap_or_else(|_| panic!("Failed to read file: {}", filename))
}

/// Splits input string by lines and returns a vector of strings.
pub fn lines_to_vec(input: &str) -> Vec<String> {
    input.lines().map(|line| line.to_string()).collect()
}

/// Parses lines into integers, skipping invalid entries.
pub fn parse_lines_to_integers(input: &str) -> Vec<i32> {
    input.lines().filter_map(|line| line.parse::<i32>().ok()).collect()
}

use std::time::Instant;

/// Checks if the provided function's result matches the expected value and times its execution.
///
/// Prints:
/// - Time taken to execute the function.
/// - "OK" if the actual outcome matches the expected.
/// - "ERROR!" followed by the expected and actual outcome if they differ.
///
/// # Arguments
/// `description` - A string describing the test case.
/// `expected` - The expected value of the test.
/// `actual_fn` - A closure that calculates the actual value.
///
/// # Examples
/// ```
/// check("Simple Test", 42, || 21 * 2);
/// ```
pub fn check<T: std::fmt::Debug + PartialEq, F: FnOnce() -> T>(description: &str, expected: T, actual_fn: F) {
    print!("{}: ", description);

    // Start timing the function
    let start_time = Instant::now();
    let actual = actual_fn();
    let duration = start_time.elapsed();

    // Report results
    print!("Time taken: {:.3?} ", duration);
    if actual == expected {
        println!("OK");
    } else {
        println!("ERROR! Expected: {:?}, Actual: {:?}", expected, actual);
    }
}

/// Executes the provided function, timing it, and prints out the calculated value.
///
/// Prints:
/// - Time taken to execute the function.
/// - The actual computed value.
///
/// # Arguments
/// `description` - A string describing the simulation/test.
/// `actual_fn` - A closure that calculates the actual value.
///
/// # Examples
/// ```
/// simulate("Simple Simulation", || 21 * 2);
/// ```
pub fn simulate<T: std::fmt::Debug, F: FnOnce() -> T>(description: &str, actual_fn: F) {
    print!("{}: ", description);

    // Start timing the function
    let start_time = Instant::now();
    let actual = actual_fn();
    let duration = start_time.elapsed();

    // Report results
    println!("Time taken: {:.3?}", duration);
    println!("Result: {:?}", actual);
}
