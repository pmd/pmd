asm void eSPI_boot()
{
  // setup stack pointer

  lis r1, _stack_addr@h
  ori r1, r1, _stack_addr@l
}

int main() {
}

#if DEBUG
int foobar() {
}
#endif

#if 0
static void my_memset(void *dest,int fill_value,int count)
{
    __asm __volatile__(
         "cld\n"
         "mov %ecx, %ebx\n"
         "shr 2,%ecx\n"
         "rep "
         "stosl\n"
         "mov %ebx,%ecx\n"
         "  // line 157 mentioned above"
         :
         : "c" (count), "a" (fill_value), "D" (dest)
         : "cc","%ebx" );
}
#endif


int otherMethod() {
}