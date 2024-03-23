#![allow(dead_code)]
mod base;
mod primitives;
mod custom_types;
mod variable_bindings;

fn main() {
    variable_bindings::scope_and_shadowing::run();
}
