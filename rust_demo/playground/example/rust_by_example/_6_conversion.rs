pub mod from_and_into {
    #[derive(Debug)]
    struct Integer {
        value: i32,
    }

    impl From<i32> for Integer {
        fn from(value: i32) -> Self {
            Integer { value }
        }
    }

    pub fn run() {
        let my_str = "hello";
        let _my_string = String::from(my_str);

        let num = Integer::from(30);
        println!("My number is {num:?}");

        let num: Integer = 16i32.into(); // 为A实现了From<B>，那么B自动实现了Into<A>
        println!("{:?}", num);

        // 需要提供额外信息给编译器，以告知目标转换类型
        println!("{:?}", <i32 as Into<Integer>>::into(12i32));
    }
}

pub mod try_from_and_try_into {
    use std::convert::TryFrom;

    #[derive(Debug, PartialEq)]
    struct EvenNumber(i32);

    impl TryFrom<i32> for EvenNumber {
        type Error = ();

        fn try_from(value: i32) -> Result<Self, Self::Error> {
            if value % 2 == 0 {
                Ok(EvenNumber(value))
            } else {
                Err(())
            }
        }
    }

    pub fn run() {
        assert_eq!(EvenNumber::try_from(8), Ok(EvenNumber(8)));
        assert_eq!(EvenNumber::try_from(5), Err(()));

        let result: Result<EvenNumber, ()> = 8i32.try_into();
        assert_eq!(result, Ok(EvenNumber(8)));

        let result: Result<EvenNumber, ()> = 5i32.try_into();
        assert_eq!(result, Err(()));
    }
}
