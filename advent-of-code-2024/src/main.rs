mod day01;
mod day02;
mod utils;

use std::env;

fn main() {
    // Get the day to run from command-line arguments or default to Day 1
    let args: Vec<String> = env::args().collect();
    let day = args.get(1).unwrap_or(&"1".to_string()).parse::<u32>().unwrap_or(1);

    match day {
        1 => day01::run(),
        // Add more days here as needed
        _ => eprintln!("Unsupported day: {}", day),
    }
}
