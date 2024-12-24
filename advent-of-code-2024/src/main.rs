mod utils;
mod day01;
mod day19;
mod day24;

use std::env;

fn main() {
    // Get the day to run from command-line arguments or default to Day 1
    let args: Vec<String> = env::args().collect();
    let day = args.get(1).unwrap_or(&"1".to_string()).parse::<u32>().unwrap_or(1);

    match day {
        1 => day01::run(),
        19 => day19::run(),
        24 => day24::run(),
        // Add more days here as needed
        _ => eprintln!("Unsupported day: {}", day),
    }
}
