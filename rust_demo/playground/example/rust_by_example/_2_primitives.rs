pub mod smoke {
    pub fn run() {
        println!("{}", i32::from_str_radix("z", 36).unwrap());
    }
}

pub mod tuples {
    pub fn run() {
        println!("long tuple: {:?}", (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        println!(
            "nested long tuple: {:#?}",
            (
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
            )
        );
        // 超过12个元素的元组无法被调试打印，因为没有实现这个特质
        // println!(
        //     "too long tuple: {:?}",
        //     (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)
        // );
    }
}

pub mod practice_matrix {
    use std::fmt;
    use std::fmt::Formatter;

    #[derive(Debug)]
    struct Matrix(f32, f32, f32, f32);

    impl fmt::Display for Matrix {
        fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
            write!(f, "( {} {} )\n( {} {} )", self.0, self.1, self.2, self.3)
        }
    }

    impl Matrix {
        fn transpose(&mut self) {
            std::mem::swap(&mut self.1, &mut self.2);
        }
    }

    pub fn run() {
        let mut matrix = Matrix(1.1, 1.2, 2.1, 2.2);
        println!("Matrix: \n{}", matrix);
        matrix.transpose();
        println!("Transpose: \n{}", matrix);
    }
}

pub mod arrays_and_slices {
    use std::mem;

    pub fn run() {
        let x = [1, 2, 3, 4, 5, 6];
        let slice = &x[..3];
        println!("First element of the slice is {}", slice[0]);
        println!("The slice has {} elements", slice.len());
        println!("The slice's memory size is {}", mem::size_of_val(slice));
        println!(
            "The slice's pointer memory size is {}",
            mem::size_of_val(&slice)
        );

        for i in 0..x.len() + 1 {
            match x.get(i) {
                Some(v) => println!("{}: {}", i, v),
                None => println!("Index out of bounds: {}", i),
            }
        }
    }
}

pub mod print_size {
    use std::mem;

    pub fn run() {
        println!("() has size: {}", mem::size_of::<()>());
        println!("bool has size: {}", mem::size_of::<bool>());
        println!("u8 has size: {}", mem::size_of::<u8>());
        println!("u16 has size: {}", mem::size_of::<u16>());
        println!("u32 has size: {}", mem::size_of::<u32>());
        println!("u64 has size: {}", mem::size_of::<u64>());
        println!("u128 has size: {}", mem::size_of::<u128>());
        println!("i8 has size: {}", mem::size_of::<i8>());
        println!("i16 has size: {}", mem::size_of::<i16>());
        println!("i32 has size: {}", mem::size_of::<i32>());
        println!("i64 has size: {}", mem::size_of::<i64>());
        println!("i128 has size: {}", mem::size_of::<i128>());
        println!("f32 has size: {}", mem::size_of::<f32>());
        println!("f64 has size: {}", mem::size_of::<f64>());
        println!("char has size: {}", mem::size_of::<char>());
        println!();
        println!("&[();0] has size: {}", mem::size_of::<&[(); 0]>());
        println!("&[bool;0] has size: {}", mem::size_of::<&[bool; 0]>());
        println!("&[u8;0] has size: {}", mem::size_of::<&[u8; 0]>());
        println!("&[u16;0] has size: {}", mem::size_of::<&[u16; 0]>());
        println!("&[u32;0] has size: {}", mem::size_of::<&[u32; 0]>());
        println!("&[u64;0] has size: {}", mem::size_of::<&[u64; 0]>());
        println!("&[u128;0] has size: {}", mem::size_of::<&[u128; 0]>());
        println!("&[i8;0] has size: {}", mem::size_of::<&[i8; 0]>());
        println!("&[i16;0] has size: {}", mem::size_of::<&[i16; 0]>());
        println!("&[i32;0] has size: {}", mem::size_of::<&[i32; 0]>());
        println!("&[i64;0] has size: {}", mem::size_of::<&[i64; 0]>());
        println!("&[i128;0] has size: {}", mem::size_of::<&[i128; 0]>());
        println!("&[f32;0] has size: {}", mem::size_of::<&[f32; 0]>());
        println!("&[f64;0] has size: {}", mem::size_of::<&[f64; 0]>());
        println!("&[char;0] has size: {}", mem::size_of::<&[char; 0]>());
    }
}
