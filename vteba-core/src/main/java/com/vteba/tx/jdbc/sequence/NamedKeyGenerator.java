package com.vteba.tx.jdbc.sequence;


public interface NamedKeyGenerator {
    
    /**
     * 根据sequence name获取sequence long值。该方法有副作用，调用之后，原来配置的
     * sequence名字将会被覆盖，而且影响{@link #nextString()}及其他。建议一个项目中
     * 规范调用的方式。
     * 一般是需要改变所配置的sequence名字调用。（可以达到动态改变sequence的目的）
     * @param seqName
     *            sequence名字
     * @return sequence string 值
     */
    public String nextString(String seqName);

    /**
     * 根据sequence name获取sequence long值。该方法有副作用，调用之后，原来配置的
     * sequence名字将会被覆盖，而且影响{@link #nextString()}及其他。建议一个项目中
     * 规范调用的方式。
     * 一般是需要改变所配置的sequence名字调用。（可以达到动态改变sequence的目的）
     * @param seqName
     *            sequence名字
     * @return sequence int 值
     */
    public int nextInt(String seqName);

    /**
     * 根据sequence name获取sequence long值。该方法有副作用，调用之后，原来配置的
     * sequence名字将会被覆盖，而且影响{@link #nextString()}及其他。建议一个项目中
     * 规范调用的方式。
     * 一般是需要改变所配置的sequence名字调用。（可以达到动态改变sequence的目的）
     * @param seqName
     *            sequence名字
     * @return sequence long 值
     */
    public long nextLong(String seqName);

    /**
     * 如果没有建sequence，可以使用这个来模拟生成主键。当前时间精确到毫秒加上一个随机数
     * 
     * @return sequence value
     */
    public long nextValue();
}
