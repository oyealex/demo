pub mod casting {
    #[allow(overflowing_literals)]
    pub fn run() {
        let decimal = 65.4321_f32;

        // let integer: u8 = decimal; // 不支持隐式类型转换

        let integer = decimal as u8;
        let character = integer as char;

        // let character = decimal as char; // 并不是所有基本类型组合之间都支持直接转换

        println!("Casting: {decimal} -> {integer} -> {character}");

        // 大范围类型向小范围类型转换时，会将数据持续增加或减少小范围数据最大值+1，直到小范围类型可以容纳其值
        println!(
            "1000 ({:>16b})as a u16 is: {:>5}, ({:>16b})",
            1000, 1000 as u16, 1000 as u16
        ); // u16本来就可以容纳1000
        println!(
            "1000 ({:>16b})as a u8  is: {:>5}, ({:>16b})",
            1000, 1000 as u8, 1000 as u8
        ); // 1000 - 256 -256 - 256 = 232
        println!(
            "-1   ({:>16b})as a u8  is: {:>5}, ({:>16b})",
            -1_i16,
            (-1_i16) as u8,
            (-1_i16) as u8
        ); // -1 + 256 = 255
        println!("1000 mod 256  is: {:>5}", 1000 % 256); // 对于正数来说等同于整除最大值+1之后的余数
        println!();
        println!(
            "128  ({:>16b})as a i16 is: {:>5}, ({:>16b})",
            128, 128 as i16, 128 as i16
        );
        println!(
            "128  ({:>16b})as a i8  is: {:>5}, ({:>16b})",
            128, 128 as i8, 128 as i8
        );
        println!(
            "1000 ({:>16b})as a i8  is: {:>5}, ({:>16b})",
            1000, 1000 as i8, 1000 as i8
        );
        println!(
            "-1   ({:>16b})as a i16 is: {:>5}, ({:>16b})",
            -1_i8,
            (-1_i8) as i16,
            (-1_i8) as i16
        );
        println!();
        println!(
            "integer address: {:p}, new value address: {:p}",
            &integer,
            &(integer as i8)
        );
        println!();
        println!(" 300.0 as u8 is: {}", 300.0_f32 as u8);
        println!("-100.0 as u8 is: {}", -100.0_f32 as u8);
        println!("   nan as u8 is: {}", f32::NAN as u8);
        println!();
        unsafe {
            // 300.0 as u8 is 44
            println!(" 300.0 as u8 is : {}", 300.0_f32.to_int_unchecked::<u8>());
            // -100.0 as u8 is 156
            println!(
                "-100.0 as u8 is : {}",
                (-100.0_f32).to_int_unchecked::<u8>()
            );
            // nan as u8 is 0
            println!("   nan as u8 is : {}", f32::NAN.to_int_unchecked::<u8>());
        }
    }
}
