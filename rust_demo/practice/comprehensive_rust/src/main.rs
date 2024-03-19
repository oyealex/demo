#![allow(dead_code)] // 注意添加感叹号表示是一个crate级别的属性，否则为就近元素的属性
mod basis;
mod control_flow;
mod variable;
mod enums;
mod thread;
mod references;
mod structs;

// https://google.github.io/comprehensive-rust/zh-CN/index.html
fn main() {
    structs::normal_struct::practice();
}
