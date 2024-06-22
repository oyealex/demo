mod minegrep;

use std::env;
use std::process;

use minegrep::Config;

fn main() {
    let mut args = env::args().skip(1);

    let config = Config {
        query: match args.next() {
            Some(arg) => arg,
            None => {
                eprintln!("Didn't get a query string");
                process::exit(1);
            }
        },
        filename: match args.next() {
            Some(arg) => arg,
            None => {
                eprintln!("Didn't get a file name");
                process::exit(1);
            }
        },
        case_sensitive: env::var("CASE_INSENSITIVE").is_err(),
    };

    if let Err(msg) = minegrep::run(config) {
        eprintln!("Application error: {}", msg);
        process::exit(1);
    }
}
