extern crate core;

use std::fs::File;
use std::io::Read;
use std::path::Path;

mod binary_tree;
mod pointer_test;

fn main() {
    let path = Path::new("LICENSE");
    let display = path.display();

    let mut file = match File::open(path) {
        Err(err) => panic!("couldn't open {}: {}", display, err),
        Ok(file) => file,
    };

    let mut s = String::new();
    match file.read_to_string(&mut s) {
        Err(err) => panic!("couldn't read {}: {}", display, err),
        Ok(_) => println!("{} contains:\n{}", display, s),
    }
}
