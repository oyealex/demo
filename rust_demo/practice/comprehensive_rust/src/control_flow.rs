pub mod block {
    pub fn practice() {
        let x = {
            let y = 10;
            println!("y: {y}");
            let z = {
                let w = { 3 + 4 };
                println!("w: {w}");
                y * w
            };
            println!("z: {z}");
            z - y
        };
        println!("x: {x}");
        let x = double(x);
        println!("double x: {x}");
    }

    fn double(x: i32) -> i32 {
        x + x
    }
}

pub mod if_expression {
    pub fn practice() {
        let mut x = 10;
        if x % 2 == 0 {
            x /= 2;
        } else {
            x = 3 * x + 1;
        }
        println!("x: {x}");

        let mut x = 10;
        x = if x % 2 == 0 { x / 2 } else { 3 * x + 1 };
        println!("x: {x}")
    }
}

pub mod for_loop {
    pub fn practice() {
        let v = vec![10, 20, 30];
        for x in v {
            println!("x: {x}");
        }
        println!();

        for i in (0..10).step_by(2) {
            println!("i: {i}");
        }
    }
}

pub mod multi_table_9_9 {
    pub fn practice() {
        for i in 1..=9 {
            for j in 1..=i {
                print!("{j} * {i} = {:2} | ", i * j);
            }
            println!()
        }
    }
}

pub mod while_loop {
    pub fn practice() {
        let mut x = 10;
        while x != 1 {
            println!("x: {x}");
            x = if x % 2 == 0 { x / 2 } else { 3 * x + 1 };
        }
        println!("x: {x}");
    }
}

pub mod break_and_continue {
    pub fn practice() {
        let v = vec![10, 20, 30];
        let iter = v.into_iter();
        'outer: for x in iter.skip(1) {
            println!("x: {x}");
            let mut i = 0;
            while i < x {
                println!("x: {x}, i: {i}");
                i += 1;
                if i == 3 {
                    continue 'outer;
                }
            }
        }
    }
}

pub mod loop_expression {
    pub fn practice() {
        let mut x = 10;
        loop {
            x = if x % 2 == 0 { x / 2 } else { 3 * x + 1 };
            println!("x: {x}");
            if x == 1 {
                break;
            }
        }
    }
}
