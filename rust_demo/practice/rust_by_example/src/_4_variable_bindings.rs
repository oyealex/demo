pub mod smoke {
    pub fn run() {
        let an_integer = 1u32;
        let a_boolean = true;
        let unit = ();

        let mut copied_integer = an_integer;

        println!("An integer: {:?}", copied_integer);
        println!("A boolean: {:?}", a_boolean);
        println!("Meet the unit value: {:?}", unit);

        copied_integer = 3;
        println!(
            "Copied integer: {:?}, source integer: {:?}",
            copied_integer, an_integer
        );
        println!(
            "Copied integer address: {:p}, source integer address: {:p}",
            &copied_integer, &an_integer
        );
        println!("Unit address: {:p}", &unit);

        let _unused_variable = 3u32;
    }
}

pub mod scope_and_shadowing {
    pub fn run() {
        let long_lived_binding = 1;

        println!("long var: {}", long_lived_binding);
        println!("long var address: {:p}", &long_lived_binding);
        {
            println!("inner long before shadowed: {}", long_lived_binding);
            println!(
                "long var address before shadowed: {:p}",
                &long_lived_binding
            );
            let long_lived_binding = 3;
            println!("inner long after shadowed: {}", long_lived_binding);
            println!("long var address after shadowed: {:p}", &long_lived_binding);

            let short_lived_binding = 2;
            println!("inner short: {}", short_lived_binding);
        }

        println!("outer long: {}", long_lived_binding);
        println!("outer long address: {:p}", &long_lived_binding);

        let long_lived_binding = 4;
        println!("outer long after shadowed: {}", long_lived_binding);
        println!(
            "outer long var address after shadowed: {:p}",
            &long_lived_binding
        );
    }
}

pub mod declare_first {
    pub fn run() {
        let binding;

        {
            let x = 2;
            binding = x * x;
        }
        println!("binding: {}", binding);
    }
}

pub mod freezing {
    pub fn run() {
        let mut _integer = 7i32;

        {
            let _integer = _integer;
            // _integer = 10; // 外部的_integer被遮蔽，无法改变值
        }

        _integer = 12;
    }
}
