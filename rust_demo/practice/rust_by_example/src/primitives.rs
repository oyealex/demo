#[allow(dead_code)]
pub mod smoke {
    pub fn run() {
        println!("{}", i32::from_str_radix("z", 36).unwrap());
    }
}

#[allow(dead_code)]
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

#[allow(dead_code)]
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
            let t = self.1;
            self.1 = self.2;
            self.2 = t;
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

    fn analyze_slice(slice: &[i32]) {
        println!("First element of the slice is {}", slice[0]);
        println!("The slice has {} elements", slice.len());
        println!("The slice's memory size is {}", mem::size_of_val(slice));
    }

    pub fn run() {
        let x = [1, 2, 3, 4, 5, 6];
        analyze_slice(&x[..3]);
    }
}
