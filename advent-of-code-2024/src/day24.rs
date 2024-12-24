use crate::utils;
use std::collections::{HashMap, VecDeque};

pub fn run() {
    let input = utils::read_file("inputs/day24.txt");
    let sample1 = utils::read_file("inputs/day24_sample1.txt");
    let sample2 = utils::read_file("inputs/day24_sample2.txt");

    utils::check("Part 1", 4, || solve_part1(&sample1));
    utils::check("Part 1", 2024, || solve_part1(&sample2));
    utils::check("Part 1", 57270694330992, || solve_part1(&input));
    // utils::check("Part 2", 0, || solve_part2(&sample));
    // utils::check("Part 2", 0, || solve_part2(&input));
}

fn solve_part1(input: &str) -> u64 {
    let (mut wires, mut gates) = parse_input(input);

    while !gates.is_empty() {
        let gate = gates.pop_front().unwrap();
        if !(wires.contains_key(&gate.r) && wires.contains_key(&gate.l)) {
            gates.push_back(gate);
            continue;
        }

        let result_value = gate.compute(&wires);
        wires.insert(gate.result, result_value);
    }

    let mut z_gates = wires.keys()
        .filter(|key| key.starts_with("z"))
        .collect::<Vec<&String>>();

    z_gates.sort();
    z_gates.reverse();

    let z_gates_binary = z_gates.iter()
        .map(|key| wires.get(*key).unwrap().to_string())
        .reduce(|a, b| format!("{}{}", a, b));

    u64::from_str_radix(&z_gates_binary.unwrap(), 2).unwrap()
}

fn solve_part2(input: &str) -> i32 {
    parse_input(input);
    0
}


fn parse_input(input: &str) -> (HashMap<String, i32>, VecDeque<Gate>) {
    let mut top_bottom = input.split("\n\n");
    let start = top_bottom.next().unwrap();
    let rules = top_bottom.next().unwrap();

    let mut wires = HashMap::new();

    start.lines()
        .map(|line| line.split(": ").collect::<Vec<&str>>())
        .for_each(|line| {
            wires.insert(line[0].to_string(), line[1].parse::<i32>().unwrap());
        });

    let gates = rules.lines()
        .map(|line| line.split(" ").collect::<Vec<&str>>())
        .map(|line| Gate {
            l: line[0].to_string(),
            r: line[2].to_string(),
            op: line[1].to_string(),
            result: line[4].to_string(),
        })
        .collect::<VecDeque<Gate>>();


    (wires, gates)
}

#[derive(Debug)]
struct Gate {
    l: String,
    r: String,
    op: String,
    result: String,
}
impl Gate {
    fn compute(&self, wires: &HashMap<String, i32>) -> i32 {
        let l = wires.get(&self.l).unwrap();
        let r = wires.get(&self.r).unwrap();
        match self.op.as_str() {
            "AND" => l & r,
            "OR" => l | r,
            "XOR" => l ^ r,
            _ => panic!("Unknown operator: {}", self.op),
        }
    }
}

