package com.septemberhx.server.algorithm.deploy;

import javax.xml.soap.Node;
import java.math.BigDecimal;
import java.sql.Struct;
import java.util.*;
import java.util.stream.LongStream;

/**
 * @Author Lei
 * @Date 2020/3/31 22:24
 * @Version 1.0
 */
public class GenerationAlgorithm {

        // 集群个数
    private Integer groupNumber = 5;

    //复制个数
    private Double copy = 0.4;

    // 迭代次数
    private Integer iterationTimes = 10;

    private List<String> nodeNames = new ArrayList<>();

    private List<Integer> dependency = new ArrayList<>();

    private List<Integer> support = new ArrayList<>();

    private List<Double> adapt = new ArrayList<>();

    private List<Double> adaptProbility = new ArrayList<>();


    public GenerationAlgorithm(Map<String, Integer> dependency, Map<String, Integer> support){
        for(String string:dependency.keySet()){
            this.nodeNames.add(string);
            this.dependency.add(dependency.get(string));
            this.support.add(support.get(string));
        }
    }

    public Map<String, Integer> evolution(){
        List<List<Integer>> chromosomeMatrix = getResult(null);
        System.out.println("初代集群: " + chromosomeMatrix);
        // 迭代次数， 若是提前满足要求，则提前退出
        for(int i =0 ; i < this.iterationTimes;i++){
            //计算适应度
            this.adapt = calAdaptability(chromosomeMatrix);
            //计算适应度概率
            this.adaptProbility = calSelectionProbability(this.adapt);
            // 得到下一代的染色体
            System.out.println(String.format("第%d次迭代的适应度概率: " + this.adaptProbility, i));
            chromosomeMatrix = getResult(chromosomeMatrix);
            System.out.println(String.format("第%d次迭代的集群: " + chromosomeMatrix, i));
        }
        return getLaastResult(chromosomeMatrix);
    }

    // 生成染色体
    public List<List<Integer>> getResult(List<List<Integer>> list){
        Random r = new Random();
        List<List<Integer>> chromosomeMatrix = new ArrayList<>();
        if(list == null || list.size() ==0){
            for(int i = 0; i < this.groupNumber;i++){
                List<Integer> s = new ArrayList<>();
                for(int j = 0;j<this.nodeNames.size();j++){
                    Integer min = this.dependency.get(j)>this.support.get(j)?this.dependency.get(j):this.support.get(j);
                    s.add(r.nextInt(min));
                }
                chromosomeMatrix.add(s);
            }
        }else{
            // 复制个数
            Integer copyNumber = (int)(this.groupNumber * copy);
            List<List<Integer>> copyGroup = getCopy(list, copyNumber);
            List<List<Integer>> crossGroup = getCross(list, groupNumber - copyNumber);
            chromosomeMatrix.addAll(copyGroup);
            chromosomeMatrix.addAll(crossGroup);
        }
        return chromosomeMatrix;
    }

    // copy个数
    public List<List<Integer>> getCopy(List<List<Integer>> list , Integer number){
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> record = new ArrayList<>();
        for(int i =0;i<number;i++){
            int num = i;
            Double compare = 0.0;
            for(int j = 0; j<list.size();j++){
                if(this.adapt.get(j) >= compare && !record.contains(j)){
                    num = j;
                    compare = this.adaptProbility.get(j);
                }
            }
            result.add(list.get(num));
            record.add(num);
        }
        return result;
    }

    //交叉变异
    public List<List<Integer>> getCross(List<List<Integer>> list, Integer crossNumber){
        Random r = new Random();
        List<List<Integer>> result = new ArrayList<>();
        int length = list.get(0).size();
        for(int i =0;i<crossNumber;i++){
            List<Integer> getList = new ArrayList<>();
            int father = getSelectedOne_byRoulette(this.adaptProbility);
            int mother = getSelectedOne_byRoulette(this.adaptProbility);
            int crossIndex = r.nextInt(length);

            for(int j = 0;j<list.get(father).size();j++){
                if(j<=crossIndex){
                    getList.add(list.get(father).get(j));
                }else{
                    break;
                }
            }
            for(int j = 0;j<list.get(mother).size();j++){
                if(j<=crossIndex){
                    continue;
                }else{
                    getList.add(list.get(mother).get(j));
                }
            }
            int mutationIndex = r.nextInt(length);
            getList.set(mutationIndex, r.nextInt(this.dependency.get(mutationIndex)>this.support.get(mutationIndex)
                    ?this.dependency.get(mutationIndex):this.support.get(mutationIndex)));
            result.add(getList);
        }
        return result;
    }

    /**
     * 根据轮盘赌算法，返回被选中的某个染色体的数组下标
     *
     * @param selectionProbability
     * @return
     */
    public int getSelectedOne_byRoulette(List<Double> selectionProbability) {
        double sums = 0.0;
        for(int i = 0; i <selectionProbability.size();i++){
            sums = sums + selectionProbability.get(i);
        }
        double probTotal = (double)new Random().nextInt((int)sums+1);
        if(probTotal >= 1.0){
            probTotal = probTotal-1;
        }else{
            probTotal = 0;
        }
        double sum = 0.0;
        for (int i = 0; i < selectionProbability.size(); i++) {
            sum += selectionProbability.get(i);
            if (sum >= probTotal) {
                return i;
            }
        }
        return 0;
    }


    //计算适应度
    public List<Double> calAdaptability(List<List<Integer>> list){
        List<Double> adapt = new ArrayList<>();
        for(List<Integer> l : list){
            adapt.add(getListGroupAdapt(l));
        }
        return adapt;
    }

    public Double getListGroupAdapt(List<Integer> list){
        Double thisGroupAdapt = 0.0;
        for(int i = 0;i<list.size();i++){
            Integer dependencyNumber = this.dependency.get(i);
            Integer supportNumber = this.support.get(i);
            if(supportNumber == 0 && dependencyNumber==0){
                thisGroupAdapt = thisGroupAdapt + 0.0;
            }else if(supportNumber != 0 && dependencyNumber==0){
                thisGroupAdapt = thisGroupAdapt + getSupportGrade(list.get(i), supportNumber);
//                thisGroupAdapt = thisGroupAdapt + Math.log(list.get(i))/Math.log(supportNumber/5);
            }else if(supportNumber ==0 && dependencyNumber != 0){
                thisGroupAdapt = thisGroupAdapt+ getDenpendency(list.get(i), dependencyNumber);
//                thisGroupAdapt = thisGroupAdapt + Math.log(list.get(i)+1)/Math.log(dependencyNumber*5 +1.0);
            }else{
                thisGroupAdapt = thisGroupAdapt + getSupportGrade(list.get(i), supportNumber) + getDenpendency(list.get(i), dependencyNumber);
//                thisGroupAdapt = thisGroupAdapt + Math.log(list.get(i)+1)/Math.log(supportNumber/5+1) + Math.log(list.get(i)+1)/Math.log(dependencyNumber*5 +1.0);
            }
        }
        return thisGroupAdapt;
    }

    public Double getSupportGrade(Integer num, Integer supportnumber){
        Integer sup = supportnumber/5;
        if(supportnumber <= 5){
            if(num <= supportnumber){
                return 1.0;
            }else{
                return Math.pow(supportnumber, 1 / (num-supportnumber));
            }
        }
        if(num <= supportnumber/5){
            return Math.log(num + 1) / Math.log(sup + 1);
        }else{
            return Math.pow(sup, 1 / (num-sup));
        }
    }

    public Double getDenpendency(Integer num, Integer dependencynumber){
        if(num <= 5*dependencynumber){
            return 1.0;
        }else{
            return  Math.pow(dependencynumber*5, 1 / (num-dependencynumber*5));
        }
    }

    //i算适应度概率
    public List<Double> calSelectionProbability(List<Double> adapt){
        if(adapt == null || adapt.isEmpty()){
            return null;
        }
        List<Double> adaptProbility = new ArrayList<>();
        double sum =0 ;
        for(Double d:adapt){
            sum = sum+d;
        }
        for(Double d:adapt){
            adaptProbility.add(new BigDecimal(d/sum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        return adaptProbility;
    }

    public Map<String, Integer> getLaastResult(List<List<Integer>> list){
        Double a = 0.0;
        int result = 0;
        for(int i = 0;i<list.size();i++){
            Double compare = getListGroupAdapt(list.get(i));
            if(compare > a){
                result = i;
                a = compare;
            }
        }
        Map<String,Integer> map = new HashMap<>();
        for(int i =0;i<this.nodeNames.size();i++){
            map.put(this.nodeNames.get(i), list.get(result).get(i));
        }
        System.out.println("最后得到的染色体" + map);
        return map;
    }


}
