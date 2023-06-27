#[cfg(test)]
mod test {
    use std::f32::consts::PI;

    #[test]
    fn formatted_print() {
        // In general, the `{}` will be automatically replaced with any
        // arguments. These will be stringified.
        println!("{} days", 31);

        // Positional arguments can be used. Specifying an integer inside `{}`
        // determines which additional argument will be replaced. Arguments start
        // at 0 immediately after the format string
        println!("{0}, this is {1}. {1}, this is {0}", "Alice", "Bob");

        // As can named arguments.
        println!(
            "{subject} {verb} {object}",
            object = "the lazy dog",
            subject = "the quick brown fox",
            verb = "jumps over"
        );

        // Different formatting can be invoked by specifying the format character after a
        // `:`.
        println!("Base 10:               {}", 69420); //69420
        println!("Base 2 (binary):       {:b}", 69420); //10000111100101100
        println!("Base 8 (octal):        {:o}", 69420); //207454
        println!("Base 16 (hexadecimal): {:x}", 69420); //10f2c
        println!("Base 16 (hexadecimal): {:X}", 69420); //10F2C

        // You can right-justify text with a specified width. This will
        // output "    1". (Four white spaces and a "1", for a total width of 5.)
        println!("{number:>5}", number = 1);

        // You can pad numbers with extra zeroes,
        //and left-adjust by flipping the sign. This will output "10000".
        println!("{number:0<5}", number = 1);

        // You can use named arguments in the format specifier by appending a `$`
        println!("{number:0>width$}", number = 1, width = 5);

        // Rust even checks to make sure the correct number of arguments are
        // used.
        println!("My name is {0}, {1} {0}", "Bond", "James");
        // FIXME ^ Add the missing argument: "James"

        // Only types that implement fmt::Display can be formatted with `{}`. User-
        // defined types do not implement fmt::Display by default

        #[allow(dead_code)]
        struct Structure(i32);

        // This will not compile because `Structure` does not implement
        // fmt::Display
        //println!("This struct `{}` won't print...", Structure(3));
        // TODO ^ Try uncommenting this line

        // For Rust 1.58 and above, you can directly capture the argument from a
        // surrounding variable. Just like the above, this will output
        // "     1". 5 white spaces and a "1".
        let number: f64 = 1.0;
        let width: usize = 5;
        println!("{number:>width$}");

        println!("pi = {:2.3}", PI);

        // Hello {arg 0 ("x")} is {arg 1 (0.01) with precision specified inline (5)}
        println!("Hello {0} is {1:.5}", "x", 0.01);

        // Hello {arg 1 ("x")} is {arg 2 (0.01) with precision specified in arg 0 (5)}
        println!("Hello {1} is {2:.0$}", 5, "x", 0.01);

        // Hello {arg 0 ("x")} is {arg 2 (0.01) with precision specified in arg 1 (5)}
        println!("Hello {0} is {2:.1$}", "x", 5, 0.01);

        // Hello {next arg -> arg 0 ("x")} is {second of next two args -> arg 2 (0.01) with precision
        //                          specified in first of next two args -> arg 1 (5)}
        println!("Hello {} is {:.*}", "x", 5, 0.01);

        // Hello {arg 1 ("x")} is {arg 2 (0.01) with precision
        //                          specified in next arg -> arg 0 (5)}
        println!("Hello {1} is {2:.*}", 5, "x", 0.01);

        // Hello {next arg -> arg 0 ("x")} is {arg 2 (0.01) with precision
        //                          specified in next arg -> arg 1 (5)}
        println!("Hello {} is {2:.*}", "x", 5, 0.01);

        // Hello {next arg -> arg 0 ("x")} is {arg "number" (0.01) with precision specified
        //                          in arg "prec" (5)}
        println!("Hello {} is {number:.prec$}", "x", prec = 5, number = 0.01);

        println!(
            "{}, `{name:.*}` has 3 fractional digits",
            "Hello",
            3,
            name = 1234.56
        );
        println!(
            "{}, `{name:.*}` has 3 characters",
            "Hello",
            3,
            name = "1234.56"
        );
        println!(
            "{}, `{name:>8.*}` has 3 right-aligned characters",
            "Hello",
            3,
            name = "1234.56"
        );
    }
}
